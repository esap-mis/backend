package ru.javavlsu.kb.clinic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.clinic.dto.MedicalCardResponseDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordRequestDTO;
import ru.javavlsu.kb.clinic.service.MedicalCardService;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.ResponseMessageError;

@RestController
@RequestMapping("/api/medicalCard")
public class MedicalCardController {

    private final MedicalCardService medicalCardService;
    private final CurrentUser currentUser;

    public MedicalCardController(MedicalCardService medicalCardService, CurrentUser currentUser) {
        this.medicalCardService = medicalCardService;
        this.currentUser = currentUser;
    }

    @GetMapping("/patient/{id}")
    public MedicalCardResponseDTO getMedicalCard(@PathVariable("id") Long patientId,
                                                 @RequestParam(required = false) String specializationDoctor) {
        return medicalCardService.getMedicalCardResponse(patientId, specializationDoctor);
    }

    @PostMapping("/patient/{id}")
    public ResponseEntity<HttpStatus> saveMedicalRecord(@PathVariable("id") Long patientId,
                                                        @Valid @RequestBody MedicalRecordRequestDTO request,
                                                        BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        medicalCardService.createMedicalRecord(request, patientId, currentUser.id());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
