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
            member.setName("탈퇴한 회원");
            member.setPassword(null);

            memberRepository.saveAll(expiredMembers);
            log.info("Successfully anonymized {} members.", expiredMembers.size());
        }
    }
}
