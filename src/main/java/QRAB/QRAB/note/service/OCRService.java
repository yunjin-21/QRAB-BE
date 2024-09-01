package QRAB.QRAB.note.service;

import QRAB.QRAB.note.config.OCRConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OCRService {
    private final OCRConfig ocrConfig;

    public String processImage(String imageUrl){
        try{
            return ocrConfig.extractTextFromImage(imageUrl);
        }catch (Exception e){
            e.printStackTrace();
            return "Failed to extract text from IMAGE";
        }
    }
}
