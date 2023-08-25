package org.project.dev.payment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String pgToken; //kakao pay 에서 발행한 토큰 이게 있어야 결제 승인을 날림!

    private String tid; // kakao pay 에서 결제 번호! 이게 있어야 결제 승인을 날림!

    private String PaymentType; //결제 업체 타임 ex) kakao, naver, toss 기타 등등등

    @Lob
    private String PaymentJson; //업체별 payment request 보냈을때 json보관용 가져올땐 jackson 써서 객체화

    private int isSucced; // 최종적으로 redirect가 와서 성공 했는지?? = 사용자에게 카톡으로 결제 완료가 왔는지?
}
