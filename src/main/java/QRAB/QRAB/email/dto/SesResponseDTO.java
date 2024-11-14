package QRAB.QRAB.email.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SesResponseDTO {
    private String message;
    @Builder
    public SesResponseDTO(String message){
        this.message = "MID:" + message;
    }

    public static SesResponseDTO from(String message){
        return SesResponseDTO.builder()
                .message(message)
                .build();
    }
}
