package com.example.demo.service;

import com.example.demo.dto.PaginationDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.exception.CustomizeErrorCode;
import com.example.demo.exception.CustomizeException;
import com.example.demo.mapper.QuestionExtMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionExample;
import com.example.demo.model.User;
import com.example.demo.model.UserExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

    //在首页展示问题
    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
        Integer offset;
        page = modifyPage(page, size, totalCount);
        if (page > 0)
            offset = size * (page - 1);
        else
            offset = 0;
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        return getPaginationDTO(page, size, totalCount, questions);
    }

    //在个人资料展示问题
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        Integer offset;
        page = modifyPage(page, size, totalCount);
        if (page > 0)
            offset = size * (page - 1);
        else
            offset = 0;
        QuestionExample example = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        return getPaginationDTO(page, size, totalCount, questions);
    }

    private PaginationDTO getPaginationDTO(Integer page, Integer size, Integer totalCount, List<Question> questions) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        for (Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(users.get(0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
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

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(question.getCreator());
        List<User> users = userMapper.selectByExample(userExample);
        questionDTO.setUser(users.get(0));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            //create
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(System.currentTimeMillis());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else {
            //update
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setTitle(question.getTitle());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(queryDTO.getTag());
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
