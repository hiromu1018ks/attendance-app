package com.example.attendanceapp.security;

import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Securityのユーザー認証を担当するカスタムサービスクラス。
 * データベースに保存されているユーザー情報を基に認証を行います。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * ユーザー情報へのデータベースアクセスを提供するリポジトリ。
     * <p>
     * {@code @RequiredArgsConstructorアノテーションにより、自動的にDIされます。}
     */
    private final UserRepository userRepository;

    /**
     * 従業員番号を基にユーザー情報を読み込み、認証に必要なUserDetailsオブジェクトを生成します。
     *
     * @param employeeNumber ログインに使用する従業員番号
     * @return Spring Securityで使用される認証情報を含むUserDetailsオブジェクト
     * @throws UsernameNotFoundException 指定された従業員番号のユーザーが存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String employeeNumber) throws UsernameNotFoundException {
        // データベースからユーザー情報を検索し、存在しない場合は例外をスロー
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new UsernameNotFoundException("職員番号が見つかりません:" + employeeNumber));

        // Spring Securityで使用するUserDetailsオブジェクトを構築
        // - ユーザー名として従業員番号を設定
        // - パスワードとしてハッシュ化されたパスワードを設定
        // - ユーザーの役割（権限）を設定
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmployeeNumber())
                .password(user.getPasswordHash())
                .roles(user.getRole().getName())
                .build();
    }
}