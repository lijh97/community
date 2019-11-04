package com.example.demo.service;

import com.example.demo.dto.PaginationDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserMapper;
import model.Question;
import model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = questionMapper.count();
        Integer offset;
        page = modifyPage(page, size, totalCount);
        if (page > 0)
            offset = size * (page - 1);
        else
            offset = 0;
        List<Question> questions = questionMapper.list(offset, size);
        return getPaginationDTO(page, size, totalCount, questions);
    }


    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        Integer totalCount = questionMapper.countByUserId(userId);
        Integer offset;
        page = modifyPage(page, size, totalCount);
        if (page > 0)
            offset = size * (page - 1);
        else
            offset = 0;
        List<Question> questions = questionMapper.listByUserId(userId, offset, size);
        return getPaginationDTO(page, size, totalCount, questions);
    }

    private PaginationDTO getPaginationDTO(Integer page, Integer size, Integer totalCount, List<Question> questions) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        paginationDTO.setPagination(totalCount, size, page);
        return paginationDTO;
    }

    private Integer modifyPage(Integer page, Integer size, Integer totalCount) {
        if (page < 1)
            page = 1;
        Integer totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        return page;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            //create
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.create(question);
        }else {
            //update
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
