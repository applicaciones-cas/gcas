/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

/**
 *
 * @author User
 */
public class ControllerHolder {
    private static DashboardController mainController;

    public static void setMainController(DashboardController controller) {
        mainController = controller;
    }

    public static DashboardController getMainController() {
        return mainController;
    }
}
