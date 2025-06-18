package motta.dev.MyBimed.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String senha;
}
