package com.example.service.implement;

import com.example.Entity.Comment;
import com.example.Entity.Product;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.repository.CommentRepository;
import com.example.repository.ProductRepository;
import com.example.request.CommentRequest;
import com.example.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Comment createComment(Long idProduct, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException, IOException {
        Optional<Product> product = productRepository.findById(idProduct);

        if(product.isPresent()){
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);

            Comment comment = new Comment();
            comment.setComment(commentRequest.getComment());
            comment.setProductOfComment(product.get());
            comment.setUserOfComment(user);
//            comment.setImageComments(imageComments);
            comment.setCreatedBy(user.getEmail());

            return commentRepository.save(comment);
        }else{
            throw new CustomException("Product not found with id:" + idProduct);
        }
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, CommentRequest commentRequest, MultipartFile[] multipartFiles) throws CustomException, IOException {
        Optional<Comment> oldComment = commentRepository.findById(id);

        if(oldComment.isPresent()){
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);

            if(oldComment.get().getUserOfComment().getId().equals(user.getId())){

                oldComment.get().setComment(commentRequest.getComment());
                oldComment.get().setUpdateBy(user.getEmail());
//                oldComment.get().setImageComments(imageComments);

                return commentRepository.save(oldComment.get());
            }else{
                throw new CustomException("You do not have permission to edit this comment !!!");
            }
        }else{
            throw new CustomException("Comment not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public String deleteCommentByAdmin(Long id) throws CustomException {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()){
            commentRepository.delete(comment.get());
            return "Delete success !!!";
        }else{
            return "Comment not found with id: " + id;
        }
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
        Pageable pageable = PageRequest.of(pageIndex-1,pageSize);
        return commentRepository.findAll(pageable).getContent();
    }

    @Override
    public String deleteCommentByUser(Long id) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        Optional<Comment> comment = commentRepository.findById(id);

        if(comment.isPresent()){
            if(comment.get().getUserOfComment().getId().equals(user.getId())){
                commentRepository.delete(comment.get());
                return "Delete success !!!";
            }else{
                throw new CustomException("You do not have permission to delete this comment !!!");
            }
        }else{
            return "Comment not found with id: " + id;
        }
    }
}
