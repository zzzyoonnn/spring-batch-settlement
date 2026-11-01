package com.batch.settlement.job;

import com.batch.settlement.domain.Orders;
import com.batch.settlement.domain.Settlement;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SettlementJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;

  // ItemReader
  @Bean
  @StepScope
  public JpaPagingItemReader<Orders> ordersReader(@Value("#{jobParameters['targetDate']}") String targetDate) {
    log.info("Reading orders from {}", targetDate);

    return new JpaPagingItemReaderBuilder<Orders>()
            .name("orderReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(1000)
            .queryString("SELECT o FROM Orders o WHERE o.orderDate = :targetDate ORDER BY o.id")
            .parameterValues(Collections.singletonMap("targetDate", LocalDate.parse(targetDate)))
            .build();
  }

  // ItemProcessor
  @Bean
  public ItemProcessor<Orders, Settlement> settlementProcessor() {
    return item -> {
      int fee = (int) (item.getAmount() * 0.03);  // 수수료 0.03 계산
      int settlementAmount = item.getAmount() - fee;    // 정산금액 계산

      return new Settlement(item.getId(), item.getStoreName(), settlementAmount, LocalDate.now());
    };
  }

  // ItemWriter
  @Bean
  public JpaItemWriter<Settlement> settlementWriter() {
    return new JpaItemWriterBuilder<Settlement>()
            .entityManagerFactory(entityManagerFactory)
            .build();
  }
}
