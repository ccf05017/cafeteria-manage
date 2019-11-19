package com.poppo.dallab.cafeteria.applications;

import com.poppo.dallab.cafeteria.domain.*;
import com.poppo.dallab.cafeteria.exceptions.MenuNotFoundException;
import com.poppo.dallab.cafeteria.exceptions.WorkDayNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
public class MenuPlanServiceTests {

    private MenuPlanService menuPlanService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuPlanRepository menuPlanRepository;

    @Mock
    private WorkDayRepository workDayRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        menuPlanService = new MenuPlanService(menuRepository, menuPlanRepository, workDayRepository);
    }

    @Test
    public void 요청된_WorkDay_Menu_모두_존재할_때_menu추가_요청_처리하기() {

        Menu menu = Menu.builder().id(3L).build();
        given(menuRepository.findByName("닭갈비")).willReturn(Optional.ofNullable(menu));

        MenuPlan menuPlan = MenuPlan.builder().id(1L).build();
        given(menuPlanRepository.save(any(MenuPlan.class))).willReturn(menuPlan);

        MenuPlan saved = menuPlanService.addMenu(1L, "닭갈비");

        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test(expected = MenuNotFoundException.class)
    public void 요청된_Menu_존재하지_않을_때_menu추가_요청_처리하기() {

        given(menuRepository.findByName("이제까지이런맛은없었다이것은갈비인가치킨인가")).willThrow(MenuNotFoundException.class);

        menuPlanService.addMenu(1L, "이제까지이런맛은없었다이것은갈비인가치킨인가");
    }

    @Test
    public void workDayId_menuPlanId_모두_존재할때_삭제하기() {

        given(workDayRepository.findById(1L)).willReturn(Optional.ofNullable(WorkDay.builder().build()));
        given(menuRepository.findById(3L)).willReturn(Optional.ofNullable(Menu.builder().build()));

        menuPlanService.deleteMenuPlan(1L, 3L);

        verify(menuPlanRepository).deleteMenuPlansByWorkDayIdAndMenuId(1L, 3L);
    }

    @Test(expected = WorkDayNotFoundException.class)
    public void workDay가_없을_때_삭제하기() {

        given(workDayRepository.findById(44L)).willThrow(WorkDayNotFoundException.class);

        menuPlanService.deleteMenuPlan(44L, 3L);
    }

    @Test(expected = MenuNotFoundException.class)
    public void menu가_없을_때_삭제하기() {

        given(workDayRepository.findById(3L)).willReturn(Optional.ofNullable(WorkDay.builder().build()));
        given(menuRepository.findById(44L)).willThrow(MenuNotFoundException.class);

        menuPlanService.deleteMenuPlan(3L, 44L);
    }
}