package webServer.admin;


import model.StatusEnum;
import model.user.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import webServer.WhatIsForDinnerService;
import webServer.userManagement.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/rest/")
public class AdminRestController {

    @Autowired
    WhatIsForDinnerService whatIsForDinnerService;

    @Autowired
    UserService userService;

    private static class AcceptCategories {
        private String action;
        private List<Integer> ids;

        public AcceptCategories() {
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public List<Integer> getIds() {
            return ids;
        }

        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }
    }
    @PostMapping(value = "/acceptCategory", produces = "application/json", consumes = "application/json")
    public List<Integer> acceptCategories(@RequestBody AcceptCategories acceptCategories) {

        whatIsForDinnerService.updateCategoriesAndOptionsToAStatus(
                acceptCategories.ids,
                null,
                StatusEnum.valueOf(acceptCategories.action)
        );

        return acceptCategories.ids;
    }

    @PostMapping(value = "/acceptOption", produces = "application/json", consumes = "application/json")
    public List<Integer> acceptOptions(@RequestBody AcceptCategories acceptCategories) {

        whatIsForDinnerService.updateCategoriesAndOptionsToAStatus(
                null,
                acceptCategories.ids,
                StatusEnum.valueOf(acceptCategories.action)
        );

        return acceptCategories.ids;
    }


    private static class UserRoleRequest {
        public int userId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public Roles getRole() {
            return role;
        }

        public void setRole(Roles role) {
            this.role = role;
        }

        public Roles role;
    }

    @PostMapping(value = "/giveRole", produces = "application/json", consumes = "application/json")
    public void GiveUserRole(@RequestBody UserRoleRequest roleRequest) {
        userService.GiveUserRole(roleRequest.userId, roleRequest.role);
    }

    @PostMapping(value = "/takeRole", produces = "application/json", consumes = "application/json")
    public void TakeUserRole(@RequestBody UserRoleRequest roleRequest) {
        userService.TakeUserRole(roleRequest.userId, roleRequest.role);
    }
}
