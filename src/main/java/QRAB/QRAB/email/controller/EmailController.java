package QRAB.QRAB.email.controller;

import QRAB.QRAB.email.dto.SesRequestDTO;
import QRAB.QRAB.email.dto.SesResponseDTO;
import QRAB.QRAB.email.service.SesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class EmailController {
    private final SesService  sesService;

    @PostMapping("/emails")
    public ResponseEntity<?> sendEmail(@RequestBody SesRequestDTO sesRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        sesRequestDTO.setTo(authentication.getName());
        String sesEmail = "yuncom21@ewhain.net";
        sesRequestDTO.setFrom(sesEmail);
        //return sesService.sendEmail(sesRequestDTO);
        sesService.sendEmailAtScheduledTime(sesRequestDTO);
        return ResponseEntity.ok("Email scheduled successfully");
    }
}
