package com.example.response;

public class TopFiveUsersBoughtTheMostResponse {
    private Long id;
    private String code;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String gender;
    private long totalPrice;

    public TopFiveUsersBoughtTheMostResponse() {
    }

    public TopFiveUsersBoughtTheMostResponse(Long id, String code, String email, String phone, String lastName, String firstName,
                                             String gender, double totalPrice) {
        this.id = id;
        this.code = code;
        this.email = email;
        this.phone = phone;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.totalPrice = Math.round(totalPrice);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
