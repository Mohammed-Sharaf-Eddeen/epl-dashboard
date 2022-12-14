package com.example.epldashboard.configurations;

import com.example.epldashboard.models.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    public FlatFileItemReader<Match> reader(Resource resource) {
        FlatFileItemReader<Match> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(resource);
        DefaultLineMapper<Match> lineMapper = new DefaultLineMapper<>();
        // DelimitedLineTokenizer defaults to comma as its delimiter
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());

        // MatchFieldSetMapper is a custom class to define which token goes for which field
        MatchFieldSetMapper matchFieldSetMapper = new MatchFieldSetMapper();
        // Adding a new token to the mapper to indicate which season
        matchFieldSetMapper.setSeason(resource.getFilename().replaceFirst("[.][^.]+$", ""));

        lineMapper.setFieldSetMapper(matchFieldSetMapper);
        itemReader.setLineMapper(lineMapper);
        itemReader.open(new ExecutionContext());

        return itemReader;
    }

    @Bean
    public MatchItemProcessor processor() {
        return new MatchItemProcessor();
    }

    @Bean
    public JpaItemWriter<Match> writer() {
        return new JpaItemWriterBuilder<Match>().entityManagerFactory(entityManager.getEntityManagerFactory()).build();
    }


    @Bean
    public List<Step> getRemainingSteps() {
        // Get all the CSV resources from the class path
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        List<Resource> resources = new ArrayList<>();
        try {
            resources = List.of(resolver.getResources("classpath*:./static/Datasets/*.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a step for each csv file
        List<Step> stepsList = new ArrayList<>();
        int stepNumber = 1;
        for (Resource resource: resources){
            stepsList.add(stepBuilderFactory.get("step" + ++stepNumber)
                    .<Match, Match> chunk(10)
                    .reader(reader(resource))
                    .processor(processor())
                    .writer(writer())
                    .build());
        }

        return stepsList;
    }

    // Step 1 has to be created alone! This was the only way to start a batch job flow!
    // it has to start after one existing step
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step 1")
                .<Match, Match> chunk(10)
                .reader(reader(new ClassPathResource("static/step1/2021-2022.csv")))
                .processor(processor())
                .writer(writer())
                .build();
    }
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        SimpleJobBuilder jobBuilder = jobBuilderFactory.get("importMatchesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .listener(listener);

        for (Step step : getRemainingSteps()) {
            jobBuilder.next(step);
        }

        return jobBuilder.build();
    }

}
