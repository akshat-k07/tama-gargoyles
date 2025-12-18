//package com.example.tama_gargoyles.controllers;
//
//import com.example.tama_gargoyles.controller.GargoyleController;
//import com.example.tama_gargoyles.model.Gargoyle;
//import com.example.tama_gargoyles.model.User;
//import com.example.tama_gargoyles.repository.GargoyleRepository;
//import com.example.tama_gargoyles.repository.UserRepository;
//import com.example.tama_gargoyles.service.CurrentUserService;
//import com.example.tama_gargoyles.service.GargoyleTimeService;
//import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(GargoyleController.class)
//class GameFlowFeatureTest {
//
//    @Autowired MockMvc mockMvc;
//
//    @MockitoBean GargoyleRepository gargoyleRepository;
//    @MockitoBean CurrentUserService currentUserService;
//    @MockitoBean GargoyleTimeService timeService;
//    @MockitoBean
//    UserRepository userRepository;
//
//    @Test
//    void redirects_whenUserHasNoGargoyles() throws Exception {
//        User u = new User();
//        u.setId(1L);
//
//        when(currentUserService.getCurrentUser()).thenReturn(u);
//        when(gargoyleRepository.findAllByUserIdOrderByIdAsc(1L)).thenReturn(List.of());
//
//        mockMvc.perform(get("/game"))
//                .andExpect(status().is3xxRedirection());
//
//        verifyNoInteractions(timeService);
//        verify(gargoyleRepository, never()).save(any());
//    }
//
//    @Test
//    void choosesChildGargoyle_whenPresent_andResumesThenTicks_andRendersGame() throws Exception {
//        User u = new User();
//        u.setId(1L);
//
//        Gargoyle bad = new Gargoyle();
//        bad.setType(Gargoyle.Type.BAD);
//
//        Gargoyle child = new Gargoyle();
//        child.setType(Gargoyle.Type.CHILD);
//
//        when(currentUserService.getCurrentUser()).thenReturn(u);
//        when(gargoyleRepository.findAllByUserIdOrderByIdAsc(1L)).thenReturn(List.of(bad, child));
//        when(timeService.gameDaysOld(child)).thenReturn(0L);
//        when(timeService.minutesIntoCurrentDay(child)).thenReturn(0L);
//
//        mockMvc.perform(get("/game"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("game"))
//                .andExpect(model().attribute("gargoyle", child))
//                .andExpect(model().attributeExists("gameDaysOld"))
//                .andExpect(model().attributeExists("minutesIntoDay"));
//
//        InOrder inOrder = inOrder(timeService);
//        inOrder.verify(timeService).resume(child);
//        inOrder.verify(timeService).tick(child);
//
//        verify(gargoyleRepository).save(child);
//    }
//
//    @Test
//    void choosesFirstGargoyle_whenNoChildPresent() throws Exception {
//        User u = new User();
//        u.setId(1L);
//
//        Gargoyle first = new Gargoyle();
//        first.setType(Gargoyle.Type.GOOD);
//
//        Gargoyle second = new Gargoyle();
//        second.setType(Gargoyle.Type.BAD);
//
//        when(currentUserService.getCurrentUser()).thenReturn(u);
//        when(gargoyleRepository.findAllByUserIdOrderByIdAsc(1L)).thenReturn(List.of(first, second));
//        when(timeService.gameDaysOld(first)).thenReturn(0L);
//        when(timeService.minutesIntoCurrentDay(first)).thenReturn(0L);
//
//        mockMvc.perform(get("/game"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("game"))
//                .andExpect(model().attribute("gargoyle", first));
//
//        verify(timeService).resume(first);
//        verify(timeService).tick(first);
//        verify(gargoyleRepository).save(first);
//    }
//}
