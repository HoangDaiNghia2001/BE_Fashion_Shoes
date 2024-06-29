package com.example.repository;

import com.example.Entity.User;
import com.example.response.TopFiveUsersBoughtTheMostResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email=?1")
    Optional<User> findByEmail(String email);

    @Query("select new com.example.response.TopFiveUsersBoughtTheMostResponse(o.user.id,o.user.email,o.user.mobile,o.user.lastName,o.user.firstName,o.user.gender,sum(o.totalPrice)) from Order o " +
            "group by o.user.id " +
            "order by sum(o.totalPrice) desc " +
            "limit 5")
    List<TopFiveUsersBoughtTheMostResponse> getTopFiveUsersBoughtTheMost();

    User findByCode(String code);

    @Query("select u from User u where (?1 is null or u.code = ?1) " +
            "and (?2 is null or u.email = ?2) " +
            "and (?3 is null or u.province = ?3) " +
            "and (?4 is null or u.district = ?4) " +
            "and (?5 is null or u.ward = ?5) " +
            "and u.email <> ?6")
    List<User> filterUserByAdmin(String code, String email, String province, String district, String ward, String emailPresent);

    @Modifying
    @Query(value = "DELETE FROM user_role WHERE user_id = :userId", nativeQuery = true)
    void deleteUserRoleByUserId(@Param("userId") Long userId);
}
