package club.asyncraft.asyncauth.common.network;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ClientMessageDTO implements Serializable {

    private String alreadyLoginMsg;

    private String loginCommandUsage;

    private String registerCommandUsage;

    private String passwordTooShort;

    @Serial
    private static final long serialVersionUID = 1L;

}
