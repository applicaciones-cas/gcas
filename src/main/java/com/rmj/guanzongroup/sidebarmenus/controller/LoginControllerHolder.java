/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

/**
 *
 * @author Guanzon
 */
public class LoginControllerHolder {

    private static DashboardController mainController;
    private static Boolean lbstatus = false;

    public static void setMainController(DashboardController controller) {
        mainController = controller;
    }

    public static DashboardController getMainController() {
        return mainController;
    }

    public static void setLogInStatus(Boolean lbvalue) {
        lbstatus = lbvalue;
    }

    public static Boolean getLogInStatus() {
        return lbstatus;
    }
}
