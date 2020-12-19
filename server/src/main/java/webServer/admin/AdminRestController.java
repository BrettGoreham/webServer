package webServer.admin;


import model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webServer.WhatIsForDinnerService;

import java.util.List;

@RestController
@RequestMapping("/admin/rest/")
public class AdminRestController {

    @Autowired
    WhatIsForDinnerService whatIsForDinnerService;

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
}
