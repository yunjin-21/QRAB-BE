package QRAB.QRAB.analysis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RAGResponseDTO {
    private boolean noRelevantDocs;
    private List<ReferenceDTO> references;
}