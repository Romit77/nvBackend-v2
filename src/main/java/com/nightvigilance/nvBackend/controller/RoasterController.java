package com.nightvigilance.nvBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nightvigilance.nvBackend.DTO.MemberDTO;
import com.nightvigilance.nvBackend.DTO.RoasterDTO;
import com.nightvigilance.nvBackend.model.Action;
import com.nightvigilance.nvBackend.model.Members;
import com.nightvigilance.nvBackend.model.Roaster;
import com.nightvigilance.nvBackend.service.ActionService;
import com.nightvigilance.nvBackend.service.MembersService;
import com.nightvigilance.nvBackend.service.RoasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/saveRoaster")
public class RoasterController {

    @Autowired
    private RoasterService roasterService;

    @Autowired
    private MembersService membersService;

    @Autowired
    private ActionService actionService;

    @PostMapping
    public Roaster addRoaster(@RequestBody String roasterDTOJson) {
        // Log the received JSON
        System.out.println("Received JSON: " + roasterDTOJson);

        // Deserialize the JSON string to RoasterDTO
        ObjectMapper objectMapper = new ObjectMapper();
        RoasterDTO roasterDTO = null;
        try {
            roasterDTO = objectMapper.readValue(roasterDTOJson, RoasterDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception (e.g., return an error response)
            return null; // Ideally, return a proper error response
        }

        // Existing logic to create and save Roaster
        Roaster roaster = new Roaster();
        roaster.setDateTime(Timestamp.valueOf(LocalDateTime.parse(roasterDTO.getDateTime())));

        Action action = actionService.getActionById(roasterDTO.getActionId());
        roaster.setAction(action);

        Roaster savedRoaster = roasterService.saveRoaster(roaster);

        List<Members> membersList = new ArrayList<>();
        for (MemberDTO memberDTO : roasterDTO.getMembers()) {
            // Log deserialized values
            System.out.println("Deserialized MemberDTO: " + memberDTO.getMemberName());
            System.out.println("isTeamHead: " + memberDTO.isTeamHead());
            System.out.println("isShiftIncharge: " + memberDTO.isShiftIncharge());
            System.out.println("isSecStaff: " + memberDTO.isSecStaff());

            Members member = new Members();
            member.setRoaster(savedRoaster);
            member.setMemberName(memberDTO.getMemberName());
            member.setMail(memberDTO.getMail());
            member.setEmpCode(memberDTO.getEmpCode());
            member.setContact(memberDTO.getContact());
            member.setHod(memberDTO.getHod());
            member.setReportingOfficer(memberDTO.getReportingOfficer());
            member.setDepartment(memberDTO.getDepartment());
            member.setDesig(memberDTO.getDesig());
            member.setIsTeamHead(memberDTO.isTeamHead());
            member.setIsShiftIncharge(memberDTO.isShiftIncharge());
            member.setIsSecStaff(memberDTO.isSecStaff());

            // Log values before persisting
            System.out.println("Persisting Member: " + member.getMemberName());
            System.out.println("isTeamHead: " + member.getIsTeamHead());
            System.out.println("isShiftIncharge: " + member.getIsShiftIncharge());
            System.out.println("isSecStaff: " + member.getIsSecStaff());

            membersList.add(membersService.saveMember(member));
        }

        savedRoaster.setMembers(membersList);

        return savedRoaster;
    }

    @GetMapping
    public List<Roaster> getAllRoasters() {
        return roasterService.getAllRoasters();
    }

    @GetMapping("/{id}")
    public Roaster getRoasterById(@PathVariable int id) {
        return roasterService.getRoasterById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteRoaster(@PathVariable int id) {
        roasterService.deleteRoaster(id);
    }
}
