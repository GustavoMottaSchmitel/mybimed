package motta.dev.MyBimed.dto;

import lombok.Data;
import motta.dev.MyBimed.enums.Cargo;

@Data
public class RegisterRequest {
    private String nome;
    private String email;
    private String senha;
    private Cargo cargo;
}
