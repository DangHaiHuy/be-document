package com.huy.pdoc.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.huy.pdoc.dto.request.ChangePasswordRequest;
import com.huy.pdoc.dto.request.CheckOtpRequest;
import com.huy.pdoc.dto.request.PasswordCreationRequest;
import com.huy.pdoc.dto.request.PictureUpdateRequest;
import com.huy.pdoc.dto.request.ResetPasswordRequest;
import com.huy.pdoc.dto.request.UserCreationRequest;
import com.huy.pdoc.dto.request.UserUpdateRequest;
import com.huy.pdoc.dto.response.ActivateResponse;
import com.huy.pdoc.dto.response.ChangePasswordResponse;
import com.huy.pdoc.dto.response.FavoriteFolderResponse;
import com.huy.pdoc.dto.response.HiddenEmailResponse;
import com.huy.pdoc.dto.response.ResetPasswordResponse;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.entity.Folder;
import com.huy.pdoc.entity.Role;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.exception.AppException;
import com.huy.pdoc.exception.ErrorCode;
import com.huy.pdoc.mapper.UserMapper;
import com.huy.pdoc.repository.FolderRepository;
import com.huy.pdoc.repository.RoleRepository;
import com.huy.pdoc.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    FolderRepository folderRepository;
    FirebaseService firebaseService;
    EmailService emailService;
    OtpService otpService;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED, "This username has already been taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED, "This email has already been taken");

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<Role>();
        Role role = roleRepository.findByAuthority("USER");
        roles.add(role);
        user.setAuthorities(roles);
        user.setActivated(false);
        String activateCode = UUID.randomUUID().toString();
        user.setActivateCode(activateCode);
        user = userRepository.save(user);
        sendEmailActivated(user.getEmail(), activateCode, user.getUsername());

        return userMapper.toUserResponse(user);
    }

    public ActivateResponse activateAccount(String code, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Not found user with this email"));
        if (user.isActivated() == true) {
            return ActivateResponse.builder().result("Your account has been activated").build();
        }
        if (user.getActivateCode().equals(code)) {
            user.setActivated(true);
            userRepository.save(user);
            System.out.println(1);
            return ActivateResponse.builder().result("Activated successfully").build();
        }
        throw new AppException(ErrorCode.INVALID_KEY, "Cannot activate");
    }

    private void sendEmailActivated(String email, String codeActivated, String username) {
        String subject = "Activating your account in web Document PTIT";
        String text = "Please use this code to activate your account(username:\"" + username + "\"): "
                + "<html><body></br><h1>" + codeActivated
                + "</h1></br><p>Please click this link to activate: <a href=\"http://localhost:3000/activate/" + email
                + "/" + codeActivated + "\">Document PTIT</a></p></body></html>";
        emailService.sendMessage("haihuy9a@gmail.com", email, subject, text);
    }

    public void createPassWord(PasswordCreationRequest passwordCreationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found username"));

        if (user.getPassword() != null) {
            throw new AppException(ErrorCode.PASSWORD_EXISTED, "This account already has a password");
        }
        user.setPassword(passwordEncoder.encode(passwordCreationRequest.getPassword()));
        userRepository.save(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse myUser = getMyInfo();
        if (myUser.getId().equals(userId)) {
            userMapper.updateUser(user, request);
            return userMapper.toUserResponse(userRepository.save(user));
        }
        throw new AppException(ErrorCode.INVALID_KEY, "You can't update another profile not your own");
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found username"));

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword((user.getPassword() == null) ? true : false);

        return userResponse;
    }

    public User getMyUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found username"));

        return user;
    }

    public FavoriteFolderResponse addFavoriteFolder(String id) {
        User myUser = getMyUser();
        Folder existFolder = folderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this document in database"));
        List<Folder> listFolder = myUser.getFavoriteFolderList();
        if (listFolder.contains(existFolder)) {
            throw new AppException(ErrorCode.EXISTED, "");
        } else {
            listFolder.add(existFolder);
        }
        myUser.setFavoriteFolderList(listFolder);
        userRepository.save(myUser);
        existFolder.setStar(existFolder.getStar() + 1);
        folderRepository.save(existFolder);
        return FavoriteFolderResponse.builder().result("This folder has been successfully added to your favorites list")
                .build();
    }

    public FavoriteFolderResponse deleteFavoriteFolder(String id) {
        User myUser = getMyUser();
        Folder existFolder = folderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this document in database"));
        List<Folder> listFolder = myUser.getFavoriteFolderList();
        if (listFolder.contains(existFolder)) {
            listFolder.remove(existFolder);
        } else {
            throw new AppException(ErrorCode.NOT_FOUND, "");
        }
        myUser.setFavoriteFolderList(listFolder);
        userRepository.save(myUser);
        existFolder.setStar(existFolder.getStar() - 1);
        folderRepository.save(existFolder);
        return FavoriteFolderResponse.builder().result("This folder has been successfully deleted")
                .build();
    }

    public UserResponse updatePicture(String userId, PictureUpdateRequest pictureUpdateRequest) {
        User myUser = getMyUser();
        firebaseService.delete("images/" + myUser.getNamePictureFirebase());
        myUser.setPicture(pictureUpdateRequest.getLinkUrl());
        myUser.setNamePictureFirebase(pictureUpdateRequest.getNamePictureFirebase());
        userRepository.save(myUser);
        return userMapper.toUserResponse(myUser);
    }

    public HiddenEmailResponse getHiddenEmail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Not found user"));
        if (user.isActivated() == false) {
            throw new AppException(ErrorCode.NOT_ACTIVATED,
                    "You need to activate your account first. Please check your mail");
        }
        String email = user.getEmail();
        if (email != null) {
            String emailRegex = "(.+)(@.*)";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            if (matcher.find()) {
                String group1 = matcher.group(1);
                System.out.println(group1);
                String lastTwoCharsOfGroup1 = group1.length() > 2 ? group1.substring(group1.length() - 2) : group1;
                int index = email.indexOf("@");
                if (index != -1) {
                    email = "***" + lastTwoCharsOfGroup1 + email.substring(index);
                }
                return HiddenEmailResponse.builder().email(email).build();
            }
        }
        throw new AppException(ErrorCode.NOT_FOUND, "Cannot find the email associated with your account");
    }

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        String oldPassword = request.getOldPassword();
        User user = getMyUser();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isValid = passwordEncoder.matches(oldPassword, user.getPassword());
        if (isValid) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ChangePasswordResponse.builder().result("Password changed successfully").build();
        } else
            throw new AppException(ErrorCode.INVALID_PASSWORD,
                    "The password you entered doesn't match this account's password");
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Cannot find this user"));
        otpService.checkOtpCode(request.getUsername(),
                CheckOtpRequest.builder().otpCode(request.getOtpCode()).build());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpService.deleteByUser(user);
        return ResetPasswordResponse.builder().result("Password changed successfully").build();
    }
}
