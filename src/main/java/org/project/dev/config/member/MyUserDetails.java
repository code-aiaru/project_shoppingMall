package org.project.dev.config.member;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.dev.member.entity.ImageEntity;
import org.project.dev.member.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class MyUserDetails implements UserDetails, OAuth2User {

    @Autowired
    private MemberEntity memberEntity;

    private Map<String, Object> attributes;


    // 일반
    public MyUserDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    // OAuth2
    public MyUserDetails(MemberEntity memberEntity, Map<String, Object> attributes) {
        this.memberEntity = memberEntity;
        this.attributes = attributes;
    }

    // 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectRole = new ArrayList<>();
        collectRole.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + memberEntity.getRole().toString(); // ROLE_
            }
        });
        return collectRole;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return memberEntity.getMemberEmail();
    }

    @Override
    public String getPassword() {
        return memberEntity.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return memberEntity.getMemberEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 회원 정보 수정 시 수정 내용 실시간으로 반영
    public void setMemberNickName(String memberNickName) {
        memberEntity.setMemberNickName(memberNickName); // MemberEntity에 저장된 사용자 닉네임을 업데이트
    }

    public void setImageUrl(String imageUrl) {
        memberEntity.getImage().setImageUrl(imageUrl);
    }
}
