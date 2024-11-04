package com.splanet.splanet.previewplan.controller;

import com.splanet.splanet.previewplan.dto.PlanCardRequestDto;
import com.splanet.splanet.previewplan.dto.PlanCardResponseDto;
import com.splanet.splanet.previewplan.dto.PlanGroupWithCardsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Preview Plans", description = "Preview Plan 및 Plan Card 관련 API")
@RequestMapping("/api/preview-plan")
public interface PreviewPlanApi {

    @PostMapping("/card")
    @Operation(summary = "플랜 카드 생성", description = "새로운 플랜 카드를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 카드가 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanCardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content)
    })
    ResponseEntity<PlanCardResponseDto> createPlanCard(
            @Parameter(description = "디바이스 ID", required = true) @RequestParam String deviceId,
            @Parameter(description = "그룹 ID", required = true) @RequestParam String groupId,
            @Parameter(description = "생성할 플랜 카드 정보", required = true) @RequestBody PlanCardRequestDto planCardRequestDto);

    @GetMapping("/card/{deviceId}/{groupId}/{cardId}")
    @Operation(summary = "플랜 카드 조회", description = "특정 플랜 카드를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 카드가 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanCardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜 카드를 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<PlanCardResponseDto> getPlanCard(
            @Parameter(description = "디바이스 ID", required = true) @PathVariable String deviceId,
            @Parameter(description = "그룹 ID", required = true) @PathVariable String groupId,
            @Parameter(description = "카드 ID", required = true) @PathVariable String cardId);

    @PutMapping("/card/{deviceId}/{groupId}/{cardId}")
    @Operation(summary = "플랜 카드 수정", description = "기존 플랜 카드를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 카드가 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = PlanCardResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "플랜 카드를 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<PlanCardResponseDto> updatePlanCard(
            @Parameter(description = "디바이스 ID", required = true) @PathVariable String deviceId,
            @Parameter(description = "그룹 ID", required = true) @PathVariable String groupId,
            @Parameter(description = "카드 ID", required = true) @PathVariable String cardId,
            @Parameter(description = "수정할 플랜 카드 정보", required = true) @RequestBody PlanCardRequestDto planCardRequestDto);

    @DeleteMapping("/card/{deviceId}/{groupId}/{cardId}")
    @Operation(summary = "플랜 카드 삭제", description = "특정 플랜 카드를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플랜 카드가 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "플랜 카드를 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<Void> deletePlanCard(
            @Parameter(description = "디바이스 ID", required = true) @PathVariable String deviceId,
            @Parameter(description = "그룹 ID", required = true) @PathVariable String groupId,
            @Parameter(description = "카드 ID", required = true) @PathVariable String cardId);

    @GetMapping("/")
    @Operation(summary = "디바이스 ID를 통해 각 그룹에 맞는 모든 플랜 카드 조회", description = "디바이스 ID에 맞는 모든 PlanGroup과 해당 PlanCard를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 PlanGroup 및 PlanCard 조회 완료",
                    content = @Content(schema = @Schema(implementation = PlanGroupWithCardsResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 PlanGroup이나 PlanCard가 존재하지 않음", content = @Content)
    })
    ResponseEntity<Set<PlanGroupWithCardsResponseDto>> getPreviewPlans(
            @Parameter(description = "조회할 디바이스 ID", required = true) @RequestParam String deviceId);

    @DeleteMapping("/delete-all")
    @Operation(summary = "모든 임시 플랜 삭제", description = "특정 deviceId에 해당하는 모든 임시 플랜을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 임시 플랜이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 deviceId에 해당하는 임시 플랜이 존재하지 않습니다.", content = @Content)
    })
    ResponseEntity<Void> deleteAllPreviewPlansByDeviceId(
            @Parameter(description = "삭제할 deviceId", required = true) @RequestParam String deviceId);

}
