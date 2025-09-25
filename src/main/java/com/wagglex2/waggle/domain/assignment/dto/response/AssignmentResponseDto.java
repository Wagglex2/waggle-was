package com.wagglex2.waggle.domain.assignment.dto.response;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentParticipants;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class AssignmentResponseDto {
    private Long id;
    private UserResponseDto user;
    private String title;
    private String content;
    private LocalDateTime deadline;
    private RecruitmentCategory category;
    private RecruitmentStatus status;
    private LocalDateTime createdAt;
    private int viewCount;

    private String department;
    private String lecture;
    private String lectureCode;
    private RecruitmentParticipants participants;
    private List<Integer> grades;
}
