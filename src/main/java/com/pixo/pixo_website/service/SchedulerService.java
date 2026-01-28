package com.pixo.pixo_website.service;

import com.pixo.pixo_website.domain.Member;
import com.pixo.pixo_website.domain.MemberStatus;
import com.pixo.pixo_website.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
@RequiredArgsConstructor
public class SchedulerService {

    private final MemberRepository memberRepository;

    //매일 새벽 4시에 실행
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void anonymizeExpiredMembers() {
        //법적 보관 기간 (5년)이 만료된 탈퇴 회원 조회
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
        List<Member> expiredMembers = memberRepository.findByStatusAndDeletedAtBefore(MemberStatus.DELETED,fiveYearsAgo);

        if (expiredMembers.isEmpty()) {
            log.info("No expired members to anonymize");
            return;
        }

        for (Member member : expiredMembers) {
            //개인 식별 정보를 영구적으로 파기 (익명화)
            member.setName("탈퇴한 회원");
            member.setPassword(null);
            // loginId와 status = 'DELETED'는 아이디 재사용 방지를 위해 유지

            //******** 여기에 reservaionRepository.deleteByMember(member) 같이 연관된 데이터들 지울 로직 추가 가능

            memberRepository.saveAll(expiredMembers);
            log.info("Successfully anonymized {} members.", expiredMembers.size());
        }
    }
}
