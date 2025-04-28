package com.cg.futurefunds.model;




import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.security.PrivateKey;

import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.tomcat.util.security.PrivilegedGetTccl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@Table(name="Users")
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "name could not be empty")
    private String Name;
    
    @Email(message = "Invalid Email Format")
    private String email;
    
    @Size(min = 8 , message ="minimum 8 characters required")
    private String password;
    
    private boolean isVerified;
    
    @NotBlank(message = "otp cann't be empty")
    @Size(min =6 ,max =6, message = "6 digits otp required")
    private String otp;
    
    
    
}
