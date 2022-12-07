package com.example.epldashboard.configurations;

import com.example.epldashboard.models.Match;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public FlatFileItemReader<Match> reader() {
        FlatFileItemReader<Match> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("./static/2020-2021.csv"));
        DefaultLineMapper<Match> lineMapper = new DefaultLineMapper<>();
        // DelimitedLineTokenizer defaults to comma as its delimiter
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        // MatchFieldSetMapper is a custom class to define which token goes for which field
        lineMapper.setFieldSetMapper(new MatchFieldSetMapper());
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
    public Job importUserJob(Step step1) {
        return jobBuilderFactory.get("importMatchesJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JpaItemWriter<Match> writer) {
        return stepBuilderFactory.get("step1")
                .<Match, Match> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}
