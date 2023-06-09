package com.parolisoft.dbquerywatch.testapp.adapters.db;

import com.parolisoft.dbquerywatch.testapp.application.out.JournalRepository;
import com.parolisoft.dbquerywatch.testapp.domain.Journal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class DefaultJournalRepository implements JournalRepository {

    private final JpaJournalRepository jpaRepository;
    private final JournalEntityMapper entityMapper;

    @Override
    public List<Journal> findByPublisher(String publisher) {
        return jpaRepository.findByPublisher(publisher).stream()
            .map(entityMapper::fromJpa)
            .collect(Collectors.toList());
    }
}
