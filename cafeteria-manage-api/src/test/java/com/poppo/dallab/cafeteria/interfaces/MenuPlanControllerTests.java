package com.poppo.dallab.cafeteria.interfaces;

import com.poppo.dallab.cafeteria.adapters.Mapper;
import com.poppo.dallab.cafeteria.applications.MenuPlanService;
import com.poppo.dallab.cafeteria.applications.MenuService;
import com.poppo.dallab.cafeteria.applications.WorkDayService;
import com.poppo.dallab.cafeteria.domain.Menu;
import com.poppo.dallab.cafeteria.domain.MenuPlan;
import com.poppo.dallab.cafeteria.domain.WorkDay;
import com.poppo.dallab.cafeteria.exceptions.NoMenuException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MenuPlanController.class)
public class MenuPlanControllerTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    MenuPlanService menuPlanService;

    @MockBean
    WorkDayService workDayService;

    @MockBean
    MenuService menuService;

    @MockBean
    Mapper mapper;

    @Test
    public void getListByMonth() throws Exception {

        List<WorkDay> workDays = Arrays.asList(WorkDay.builder()
                .id(1L)
                .date(LocalDate.of(2019,11,1))
                .build());

        given(workDayService.getWorkDaysByMonth(2019, 11)).willReturn(workDays);

        mvc.perform(get("/menuPlans?year=2019&month=11"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[")))
                .andExpect(content().string(containsString("2019-11-01")))
                .andExpect(content().string(containsString("\"workDayId\":1")))
            ;
    }

    @Test
    public void getOne() throws Exception {

        WorkDay mockWorkDay = WorkDay.builder()
                .id(1L)
                .date(LocalDate.of(2019,9,30))
                .day(LocalDate.of(2019,9,30).getDayOfWeek().name())
                .build();
        given(workDayService.getWorkDayByString("2019-09-30")).willReturn(mockWorkDay);

        List<Menu> menus = new ArrayList<>();
        menus.add(Menu.builder().name("제육볶음").build());
        menus.add(Menu.builder().name("볶음밥").build());
        given(menuService.getMenusByWorkDayId(1L)).willReturn(menus);

        // when
        mvc.perform(get("/workDay/2019-09-30"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("day")))
                .andExpect(content().string(containsString("date")))
                .andExpect(content().string(containsString("menus")))
                .andExpect(content().string(containsString("2019-09-30")))
                .andExpect(content().string(containsString("MONDAY")))
                .andExpect(content().string(containsString("제육볶음")))
                .andExpect(content().string(containsString("볶음밥")))
                .andExpect(content().string(containsString("[")))
        ;

    }

    @Test
    public void 존재하는_메뉴를_해당날짜의_식단에_추가하기() throws Exception {

        MenuPlan menuPlan = MenuPlan.builder().id(1L).build();

        given(menuPlanService.addMenu(1L, "닭갈비")).willReturn(menuPlan);

        mvc.perform(post("/workDays/1/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"menuName\": \"닭갈비\"}"))
                .andExpect(header().stringValues("Location", "/menuPlans/1"))
                .andExpect(status().isCreated());
    }

    @Test
    public void 존재하지_않는_메뉴를_해당날짜의_식단에_추가하기() throws Exception {

        given(menuPlanService.addMenu(1L, "이제까지이런맛은없었다이것은갈비인가통닭인가"))
                .willThrow(NoMenuException.class);

        mvc.perform(post("/workDays/1/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"menuName\": \"이제까지이런맛은없었다이것은갈비인가통닭인가\"}"))
                .andExpect(status().isNotFound());
    }
}
