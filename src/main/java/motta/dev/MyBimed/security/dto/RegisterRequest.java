package motta.dev.MyBimed.security.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class RegisterRequest {
    @NotBlank(message = "Nome não pode ser vazio")
    private String nome;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email não pode ser vazio")
    private String email;

    @NotBlank(message = "Senha não pode ser vazia")
    private String senha;

    @NotBlank(message = "Cargo não pode ser vazio")
    private String cargo;
}