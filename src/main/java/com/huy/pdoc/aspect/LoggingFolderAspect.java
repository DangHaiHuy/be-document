package com.huy.pdoc.aspect;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huy.pdoc.entity.ApiLog;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.repository.ApiLogRepository;
import com.huy.pdoc.service.UserService;

@Aspect
@Component
public class LoggingFolderAspect {
    private ApiLogRepository apiLogRepository;
    private UserService userService;

    @Autowired
    public LoggingFolderAspect(ApiLogRepository apiLogRepository,UserService userService) {
        this.apiLogRepository = apiLogRepository;
        this.userService=userService;
    }

    @AfterReturning(pointcut = "execution(* com.huy.pdoc.controller.DocumentController.getAllDocument(..))")
    public void logApiViewFolder(JoinPoint joinPoint) {
        User myUser = userService.getMyUser();
        Object[] args = joinPoint.getArgs();
        String slug = (String) (args[0]);
        ApiLog apiLog = ApiLog.builder().user(myUser).createAt(new Date())
                .content("User " + myUser.getLastName() + " " + myUser.getFirstName() + " accessed the folder \"" + slug
                        + "\"")
                .build();
        apiLogRepository.save(apiLog);
    }
}
