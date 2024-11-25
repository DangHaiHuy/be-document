package com.huy.pdoc.aspect;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huy.pdoc.entity.ApiLog;
import com.huy.pdoc.entity.Document;
import com.huy.pdoc.entity.Folder;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.repository.ApiLogRepository;
import com.huy.pdoc.repository.FolderRepository;
import com.huy.pdoc.service.UserService;

@Aspect
@Component
public class LoggingDocumentAspect {
    private ApiLogRepository apiLogRepository;
    private UserService userService;
    private FolderRepository folderRepository;

    @Autowired
    public LoggingDocumentAspect(ApiLogRepository apiLogRepository, UserService userService,
            FolderRepository folderRepository) {
        this.apiLogRepository = apiLogRepository;
        this.userService = userService;
        this.folderRepository = folderRepository;
    }

    @AfterReturning(pointcut = "execution(* com.huy.pdoc.service.DocumentService.getDocument(..))", returning = "result")
    public void logApiViewFile(JoinPoint joinPoint, Document result) {
        User myUser = userService.getMyUser();
        Folder folder = result.getFolder();
        folder.setView(folder.getView() + 1);
        folderRepository.save(folder);
        ApiLog apiLog = ApiLog.builder().user(myUser).createAt(new Date())
                .content("User " + myUser.getLastName() + " " + myUser.getFirstName() + " accessed the document \""
                        + result.getName() + "\" in folder \"" + result.getFolder().getSlug() + "\"")
                .build();
        apiLogRepository.save(apiLog);
    }

}
