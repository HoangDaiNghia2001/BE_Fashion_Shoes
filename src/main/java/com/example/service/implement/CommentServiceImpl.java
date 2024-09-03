package com.example.service.implement;

import com.example.Entity.Comment;
import com.example.Entity.Product;
import com.example.Entity.User;
import com.example.mapper.CommentMapper;
import com.example.repository.CommentRepository;
import com.example.request.CommentRequest;
import com.example.response.Response;
import com.example.exception.CustomException;
import com.example.service.CommentService;
import com.example.service.ProductService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MethodUtils methodUtils;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Comment getById(Long id) throws CustomException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Comment not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return comment;
    }

    @Override
    @Transactional
    public Comment createComment(Long idProduct, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException {
        Product product = productService.getById(idProduct);

        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        Comment comment = new Comment();
        commentMapper.commentRequestToComment(commentRequest, comment);
        comment.setProductOfComment(product);
        comment.setUserOfComment(user);
        comment.setCreatedBy(user.getEmail());

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException {
        Comment oldComment = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        if (!oldComment.getUserOfComment().getId().equals(user.getId())) {
            throw new CustomException(
                    "You do not have permission to edit this comment !!!",
                    HttpStatus.UNAUTHORIZED.value());
        }
        commentMapper.commentRequestToComment(commentRequest, oldComment);
        oldComment.setUpdateBy(user.getEmail());

        return commentRepository.save(oldComment);
    }

    @Override
    @Transactional
    public Response deleteCommentByAdmin(Long id) throws CustomException {
        Comment comment =this.getById(id);
        commentRepository.delete(comment);
        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Delete comment success");

        return response;
    }

    @Override
    public List<Comment> getAllCommentOfProduct(Long idProduct) throws CustomException {
        return commentRepository.getAllCommentByProduct(idProduct);
    }

    @Override
    public List<Comment> getAllCommentOfUser(Long idUser) throws CustomException {
        return commentRepository.getAllCommentByUser(idUser);
    }

    @Override
    public List<Comment> getAllComment(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return commentRepository.findAll(pageable).getContent();
    }

    @Override
    public Response deleteCommentByUser(Long id) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        Comment comment = this.getById(id);
        if (!comment.getUserOfComment().getId().equals(user.getId())) {
            throw new CustomException(
                    "You do not have permission to delete this comment !!!",
                    HttpStatus.UNAUTHORIZED.value());
        }
        commentRepository.delete(comment);
        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Delete comment success");

        return response;
    }
}
