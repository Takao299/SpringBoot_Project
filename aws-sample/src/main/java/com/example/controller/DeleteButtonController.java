package com.example.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.AttachedFile;
import com.example.entity.Facility;
import com.example.entity.Member;
import com.example.info.S3Info;
import com.example.repository.AttachedFileRepository;
import com.example.repository.FacilityRepository;
import com.example.repository.MemberRepository;
import com.example.repository.ReservationRepository;
import com.example.service.AttachedFileService;
import com.example.service.S3Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class DeleteButtonController {

	private final ReservationRepository reservationRepository;
	private final MemberRepository memberRepository;
	private final FacilityRepository facilityRepository;
	private final AttachedFileRepository attachedFileRepository;
    private final S3Service s3Service;
    private final S3Info s3Info;
    private final AttachedFileService attachedFileService;

    @PostMapping("/member/deleteButton")
    public String deleteMemberButton(@RequestParam Long id) {

        Member member = memberRepository.findByIdAndDeleteDateTimeIsNull(id).get(); //Long.valueOf(id.toString())
    	if(reservationRepository.findRemainByMemberId(id, LocalDate.now(), LocalTime.now()).size() > 0) {
    		return "有効な予約が存在します";
    	}else {
    		//実際に削除を実行
        	if ( member.getDeleteDateTime() == null ) {
        		member.setDeleteDateTime(LocalDateTime.now());
    	    	memberRepository.save(member);
        	}
    		return "member";
    	}
    }

    @PostMapping("/facility/deleteButton")
    public String deleteFacilityButton(@RequestParam Long id, Authentication loginUser) {

    	Facility facility = facilityRepository.findByIdAndDeleteDateTimeIsNull(id).get(); //Long.valueOf(id.toString())
    	if(reservationRepository.findRemainByFacilityId(id, LocalDate.now(), LocalTime.now()).size() > 0) {
    		return "有効な予約が存在します";
    	}else {
    		//実際に削除を実行
        	if ( facility.getDeleteDateTime() == null ) {
        		LocalDateTime time = LocalDateTime.now();
        		facility.setDeleteDateTime(time);
        		facility.setDeleteUser(loginUser.getName());
        		facilityRepository.save(facility);

        		//関連付けられいたアップロードファイルを全て削除する
    	    	List<AttachedFile> afs = attachedFileRepository.findByForeignIdOrderByAfId(facility.getId().intValue());
    	        for (AttachedFile af : afs) {
    	        	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    	        		// ローカルストレージからファイルを削除
    	        		attachedFileService.deleteAttachedFile(af.getAfId());
    	        	}else {
    	        		//AWS S3ストレージからファイルを削除
    	        		s3Service.delete(af);
    	        	}
    	        	af.setDeleteDateTime(time);
    	        	attachedFileRepository.save(af);	// attached_fileテーブルから論理削除
    	        }
        	}
    		return "facility";
    	}
    }

}
