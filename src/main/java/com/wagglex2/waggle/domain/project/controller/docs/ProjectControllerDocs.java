package com.wagglex2.waggle.domain.project.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 공고", description = "프로젝트 공고 관련 API")
public interface ProjectControllerDocs {

    @Operation(
            summary = "프로젝트 공고 등록",
            description = "프로젝트 공고를 등록한다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "**프로젝트 공고 작성 내용**",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCreationRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                         "title": "토스 주관 공모전 팀원 구합니다.",
                                                         "content": "1등 팀 채용한답니다. 열심히 합시다.",
                                                         "purpose": "CONTEST",
                                                         "meetingType": "OFFLINE",
                                                         "positions": [
                                                             {
                                                                 "position": "FRONT_END",
                                                                 "maxParticipants": 3
                                                             },
                                                             {
                                                                 "position": "BACK_END",
                                                                 "maxParticipants": 3
                                                             }
                                                         ],
                                                         "skills": ["REACT", "SPRING_BOOT"],
                                                         "grades": [
                                                             { "grade": 2 },
                                                             { "grade": 3 }
                                                         ],
                                                         "period": {
                                                             "startDate": "2025-12-25",
                                                             "endDate": "2026-03-20"
                                                         },
                                                         "deadline": "2025-12-20T23:59:59"
                                                     }
                                                    
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**프로젝트 공고 등록 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고를 성공적으로 등록하였습니다.",
                                                        "data": 142
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "**유효하지 않은 요청 값 또는 비즈니스 검증 실패로 인해 요청이 거부된 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "마감일이 종료일 이후로 설정된 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "마감일은 종료일 이전이어야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "종료일이 시작일 이전으로 설정된 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "종료일은 시작일 이후여야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "요청 값이 유효하지 않은 경우",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "grades[].grade",
                                                                "message": "모집 학년은 1 이상 4 이하여야 합니다."
                                                            },
                                                            {
                                                                "field": "meetingType",
                                                                "message": "진행 방식이 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Long>> createProject(
            @RequestBody @Valid ProjectCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "프로젝트 공고 상세 조회",
            description = "프로젝트 공고 1건을 상세 조회한다.",
            parameters = {
                  @Parameter(
                          name = "projectId",
                          description = "**조회하려는 프로젝트 공고 ID**",
                          required = true,
                          in = ParameterIn.PATH,
                          example = "3"
                  )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**프로젝트 공고 상세 조회 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고를 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "id": 142,
                                                            "authorId": 5,
                                                            "authorNickname": "새우깡",
                                                            "category": {
                                                                "desc": "프로젝트",
                                                                "name": "PROJECT"
                                                            },
                                                            "university": {
                                                                "desc": "영남대",
                                                                "domain": "yu.ac.kr",
                                                                "name": "YOUNGNAM_UNIV"
                                                            },
                                                            "title": "토스 주관 공모전 팀원 구합니다.",
                                                            "content": "1등 팀 채용한답니다. 열심히 합시다.",
                                                            "deadline": "2025-12-20 23:59:59",
                                                            "createdAt": "2025-11-17 15:10:51",
                                                            "status": {
                                                                "desc": "모집 중",
                                                                "name": "RECRUITING"
                                                            },
                                                            "viewCount": 1,
                                                            "purpose": {
                                                                "desc": "공모전",
                                                                "name": "CONTEST"
                                                            },
                                                            "meetingType": {
                                                                "desc": "오프라인",
                                                                "name": "OFFLINE"
                                                            },
                                                            "positions": [
                                                                {
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "participantInfo": {
                                                                        "maxParticipants": 3,
                                                                        "currParticipants": 0
                                                                    }
                                                                },
                                                                {
                                                                    "position": {
                                                                        "desc": "프론트엔드",
                                                                        "name": "FRONT_END"
                                                                    },
                                                                    "participantInfo": {
                                                                        "maxParticipants": 3,
                                                                        "currParticipants": 0
                                                                    }
                                                                }
                                                            ],
                                                            "skills": [
                                                                {
                                                                    "desc": "React",
                                                                    "name": "REACT"
                                                                },
                                                                {
                                                                    "desc": "Spring Boot",
                                                                    "name": "SPRING_BOOT"
                                                                }
                                                            ],
                                                            "grades": [
                                                                2,
                                                                3
                                                            ],
                                                            "period": {
                                                                "startDate": "2025-12-25",
                                                                "endDate": "2026-03-20"
                                                            }
                                                        }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**타 대학 공고인 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT",
                                                        "message": "타 대학의 공고입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "**해당 프로젝트 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "PROJECT_NOT_FOUND",
                                                        "message": "프로젝트 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<ProjectDetailResponseDto>> getProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "프로젝트 공고 목록 조회(검색)",
            description = """
                    프로젝트 공고 목록을 조회(검색)한다.<br>
                    enum 값은 대소문자와 `-`, `_`를 구분하지 않는다.
                    
                    예시:
                    - `/projects?purpose=contest` (O)
                    - `/projects?purpose=CONTEST` (O)
                  
                    - `/projects?positions=back_end` (O)
                    - `/projects?positions=Back-END` (O)
                    - `/projects?positions=backend` (X)
                    """,
            parameters = {
                    @Parameter(
                            name = "q",
                            description = "**검색어**<br>띄어쓰기는 `+`로 구분한다.",
                            in = ParameterIn.QUERY,
                            example = "스프링+공모전"
                    ),
                    @Parameter(
                            name = "purpose",
                            description = "**조회하려는 프로젝트 목적**",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "positions",
                            description = """
                                    **조회하려는 포지션**<br>
                                    값은 `,`로 구별한다.<br>
                                    예시: `/projects?positions=front-end,back-end`
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "skills",
                            description = """
                                    **조회하려는 기술 스택**<br>
                                    값은 `,`로 구별한다.<br>
                                    예시: `/projects?positions=react,spring-boot`
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "status",
                            description = """
                                    **조회하려는 공고 상태(마감 여부)**<br>
                                    기본값(미지정): `모집 중`, `마감` 모두
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    **조회하려는 프로젝트 공고 목록 페이지**<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    **조회하려는 프로젝트 공고 개수**<br>
                                    기본값: 9
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**프로젝트 공고 목록 조회 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "/projects?page=4&size=3&purpose=contest",
                                            description = "`/projects?page=4&size=3&purpose=contest`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 113,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "title": "학교 공모전 팀원 구합니다.",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "meetingType": {
                                                                        "desc": "오프라인",
                                                                        "name": "OFFLINE"
                                                                    },
                                                                    "positions": [
                                                                        {
                                                                            "desc": "게임",
                                                                            "name": "GAME"
                                                                        },
                                                                        {
                                                                            "desc": "디자인",
                                                                            "name": "DESIGNER"
                                                                        }
                                                                    ],
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Figma",
                                                                            "name": "FIGMA"
                                                                        },
                                                                        {
                                                                            "desc": "Unity",
                                                                            "name": "UNITY"
                                                                        }
                                                                    ],
                                                                    "purpose": {
                                                                        "desc": "공모전",
                                                                        "name": "CONTEST"
                                                                    }
                                                                },
                                                                {
                                                                    "id": 23,
                                                                    "authorId": 20,
                                                                    "authorNickname": "우주멋쟁이",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "title": "졸업작품 팀원 모집합니다",
                                                                    "deadline": "2025-10-25",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "positions": [
                                                                        {
                                                                            "desc": "백엔드",
                                                                            "name": "BACK_END"
                                                                        },
                                                                        {
                                                                            "desc": "프론트엔드",
                                                                            "name": "FRONT_END"
                                                                        }
                                                                    ],
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        },
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "React",
                                                                            "name": "REACT"
                                                                        }
                                                                    ],
                                                                    "purpose": {
                                                                        "desc": "공모전",
                                                                        "name": "CONTEST"
                                                                    }
                                                                },
                                                                {
                                                                    "id": 19,
                                                                    "authorId": 5,
                                                                    "authorNickname": "새우깡",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "title": "학교 공모전 팀원 구합니다.",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "meetingType": {
                                                                        "desc": "오프라인",
                                                                        "name": "OFFLINE"
                                                                    },
                                                                    "positions": [
                                                                        {
                                                                            "desc": "게임",
                                                                            "name": "GAME"
                                                                        },
                                                                        {
                                                                            "desc": "디자인",
                                                                            "name": "DESIGNER"
                                                                        }
                                                                    ],
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Figma",
                                                                            "name": "FIGMA"
                                                                        },
                                                                        {
                                                                            "desc": "Unity",
                                                                            "name": "UNITY"
                                                                        }
                                                                    ],
                                                                    "purpose": {
                                                                        "desc": "공모전",
                                                                        "name": "CONTEST"
                                                                    }
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 3,
                                                                "number": 4,
                                                                "totalElements": 16,
                                                                "totalPages": 6
                                                            }
                                                        }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "**요청 값이 유효하지 않은 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "/projects?page=5&size=3&purpose=banana",
                                            description = "`/projects?page=5&size=3&purpose=banana`",
                                            value = """
                                                    {
                                                        "code": "INVALID_ENUM_VALUE",
                                                        "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                        "data": "쿼리 파라미터 'purpose'의 값 'banana'이(가) 유효하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Page<ProjectSummaryResponseDto>>> getProjectSummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "purpose", required = false) ProjectPurpose purpose,
            @RequestParam(value = "positions", required = false) List<PositionType> positions,
            @RequestParam(value = "skills", required = false) List<Skill> skills,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable
    );

    @Operation(
            summary = "프로젝트 공고 수정",
            description = "본인이 작성한 프로젝트 공고 1건을 수정한다.",
            parameters = {
                    @Parameter(
                            name = "projectId",
                            description = "**수정하려는 프로젝트 공고 ID**",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "**프로젝트 공고 수정 내용**",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectUpdateRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "title": "토스 주관 공모전 팀원 구합니다. (추가 모집)",
                                                        "content": "1등 팀 채용한답니다. 열심히 합시다.",
                                                        "purpose": "CONTEST",
                                                        "meetingType": "OFFLINE",
                                                        "positions": [
                                                            {
                                                                "position": "FRONT_END",
                                                                "participantInfo": {
                                                                    "maxParticipants": 3,
                                                                    "currParticipants": 2
                                                                }
                                                            },
                                                            {
                                                                "position": "BACK_END",
                                                                "participantInfo": {
                                                                    "maxParticipants": 3,
                                                                    "currParticipants": 1
                                                                }
                                                            }
                                                        ],
                                                        "skills": ["VUE_JS", "DJANGO"],
                                                        "grades": [
                                                            { "grade": 2 },
                                                            { "grade": 3 }
                                                        ],
                                                        "period": {
                                                            "startDate": "2025-12-25",
                                                            "endDate": "2026-03-20"
                                                        },
                                                        "deadline": "2025-12-30T23:59:59"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**프로젝트 공고 수정 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고를 성공적으로 수정하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "**입력 값이 유효하지 않은 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "현재 참가 인원이 모집 인원보다 많은 경우",
                                            value = """
                                                    {
                                                        "code": "MAX_PARTICIPANTS_EXCEEDED",
                                                        "message": "참가 인원이 최대 모집 인원을 초과했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "마감일이 종료일 이후인 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "마감일은 종료일 이전이어야 합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**권한 없음**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN",
                                                        "message": "접근 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "**해당 프로젝트 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "PROJECT_NOT_FOUND",
                                                        "message": "프로젝트 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    ResponseEntity<APIResponse<Void>> updateProject(
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "프로젝트 공고 삭제",
            description = "본인이 작성한 프로젝트 공고 1건을 삭제한다.",
            parameters = {
                    @Parameter(
                            name = "projectId",
                            description = "**삭제하려는 프로젝트 공고 ID**",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**프로젝트 공고 삭제 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고를 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**권한 없음**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN",
                                                        "message": "접근 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "**해당 프로젝트 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "PROJECT_NOT_FOUND",
                                                        "message": "프로젝트 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    ResponseEntity<APIResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
