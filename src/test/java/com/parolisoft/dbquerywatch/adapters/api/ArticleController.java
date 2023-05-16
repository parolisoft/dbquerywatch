package com.parolisoft.dbquerywatch.adapters.api;

import com.parolisoft.dbquerywatch.application.service.ArticleService;
import com.parolisoft.dbquerywatch.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
class ArticleController {

    private final ArticleService articleService;
    private final ArticleQueryMapper queryMapper;

    @GetMapping("/{id}")
    public Optional<Article> getArticle(@PathVariable long id) {
        return articleService.findById(id);
    }

    @PostMapping("/query")
    public List<Article> query(@RequestBody ArticleQueryModel queryModel) {
        Pageable pageable = Pageable.unpaged();
        return articleService.query(queryMapper.fromModel(queryModel), pageable);
    }
}
