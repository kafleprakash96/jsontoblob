package com.example.jsontoblob.service;

import com.example.jsontoblob.dto.ArticleDto;
import com.example.jsontoblob.model.Article;
import com.example.jsontoblob.repo.ArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;


    public ArticleDto saveArticle(ArticleDto articleDto) throws SQLException {
        Article article = modelMapper.map(articleDto,Article.class);

        String content = articleDto.getContent();
        Blob blobOfContent = new SerialBlob(content.getBytes(StandardCharsets.UTF_8));

        article.setContent(blobOfContent);
        articleRepository.save(article);
        return modelMapper.map(article,ArticleDto.class);
    }

    public ArticleDto getArticleById(Long id) throws SQLException, IOException {
        Article article = articleRepository.findById(id).orElseThrow(()-> new RuntimeException("Article not found"));

        //Get the blob content
        Blob blob = article.getContent();

        //Convert blob to String
        String releaseNoteString = convertBlobToString(blob);

        ArticleDto articleDto = modelMapper.map(article,ArticleDto.class);

        articleDto.setContent(releaseNoteString);

        return articleDto;
    }

    private String convertBlobToString(Blob blob) throws SQLException {
        return new String(blob.getBytes(1,(int)blob.length()));
    }
}
