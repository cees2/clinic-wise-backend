package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.CreateAppointmentRequest;
import com.clinicwise.backend.dto.request.UpdateAppointmentRequest;
import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments(){
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{appointmentId}")
    public AppointmentResponse getAppointment(@PathVariable int appointmentId){
        return appointmentService.getAppointment(appointmentId);
    }

    @PostMapping
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest createAppointmentRequest){
        return appointmentService.createAppointment(createAppointmentRequest);
    }

    @PatchMapping("/{appointmentId}")
    public AppointmentResponse updateAppointment(@PathVariable int appointmentId, @RequestBody UpdateAppointmentRequest updateAppointmentRequest){
        return appointmentService.updateAppointment(appointmentId,updateAppointmentRequest);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable int appointmentId){
        appointmentService.deleteAppointment(appointmentId);

        return ResponseEntity.noContent().build();
    }
}
