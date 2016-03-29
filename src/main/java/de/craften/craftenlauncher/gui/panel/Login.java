/**
 * CraftenLauncher is an alternative Launcher for Minecraft developed by Mojang.
 * Copyright (C) 2014  Johannes "redbeard" Busch, Sascha "saschb2b" Becker
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Login Class:
 * <p>
 * Showing the Loading screen to display some useful information about the current download
 *
 * @author saschb2b
 */

package de.craften.craftenlauncher.gui.panel;

import de.craften.craftenlauncher.gui.Manager;
import de.craften.craftenlauncher.exception.CraftenLogicException;
import de.craften.craftenlauncher.logic.Facade;
import de.craften.ui.swingmaterial.MaterialButton;
import de.craften.ui.swingmaterial.MaterialColor;
import de.craften.ui.swingmaterial.MaterialShadow;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("serial")
public class Login extends JPanel {
    private static final String DEFAULT_USERNAME = "example@email.com";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public Login() {
        setBackground(Color.WHITE);
        setLayout(null);
    }

    public void init() {
        removeAll();

        buildUI();
        //addHelpLabels();

        if (!Facade.getInstance().isForceLogin()) {
            try {
                if (Facade.getInstance().getUser() != null) {
                    if (Facade.getInstance().getUser().getEmail() != null && !Facade.getInstance().getUser().getEmail().equals("")) {
                        usernameField.setText(Facade.getInstance().getUser().getEmail());
                        passwordField.grabFocus();
                    }
                    Facade.getInstance().authenticateUser();

                    Manager.getInstance().showProfile();
                }
            } catch (CraftenLogicException e) {
                errorLabel.setText(e.getMessage());
            }
        }
        repaint();
    }

    public void sliderAction() {
        doLogin();
    }

    private void doLogin() {
        String username = null;
        if (!usernameField.getText().equals(DEFAULT_USERNAME)) {
            username = usernameField.getText();
        }
        try {
            errorLabel.setText("");
            Facade.getInstance().setUser(username, passwordField.getPassword());
            Facade.getInstance().authenticateUser();

            Manager.getInstance().showProfile();
        } catch (CraftenLogicException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    private void buildUI() {
        usernameField = new JTextField();
        usernameField.setText(DEFAULT_USERNAME);
        usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals(DEFAULT_USERNAME)) {
                    usernameField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // TODO Auto-generated method stub
                if (usernameField.getText().equals("")) {
                    usernameField.setText(DEFAULT_USERNAME);
                }
            }
        });
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        });
        usernameField.setBorder(null);
        usernameField.setBackground(MaterialColor.CYAN_100);
        usernameField.setBounds(0, 0, 238, 36);
        usernameField.setLocation(69, 0);
        add(usernameField);

        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(255, 103, 73));
        add(errorLabel);

        passwordField = new JPasswordField();
        passwordField.enableInputMethods(true);
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        });
        passwordField.setBorder(null);
        passwordField.setBackground(MaterialColor.CYAN_100);
        passwordField.setBounds(0, 0, 238, 36);
        passwordField.setLocation(69, 47);
        add(passwordField);

        JButton loginButton = new MaterialButton();
        loginButton.setBackground(MaterialColor.CYAN_500);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doLogin();
            }
        });
        loginButton.setText("Login");
        loginButton.setBounds(0, 0, 238 + MaterialShadow.OFFSET_LEFT + MaterialShadow.OFFSET_RIGHT,
                36 + MaterialShadow.OFFSET_TOP + MaterialShadow.OFFSET_BOTTOM);
        loginButton.setLocation(69 - MaterialShadow.OFFSET_LEFT, 94 - MaterialShadow.OFFSET_TOP);
        add(loginButton);

        setComponentZOrder(passwordField, 2);
    }

    private void addHelpLabels() {
        JLabel _linkUsernameorEmail = new JLabel("Username or E-mail?");
        _linkUsernameorEmail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent env) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI("https://help.mojang.com/customer/portal/articles/1233873"));
                } catch (IOException e) {
                } catch (URISyntaxException e) {
                }
            }
        });
        _linkUsernameorEmail.setLocation(usernameField.getX() - 8, usernameField.getY() + usernameField.getHeight() + 2);
        markJLabelLink(_linkUsernameorEmail);
        add(_linkUsernameorEmail);

        JLabel _linkForgotPassword = new JLabel("Forgot password?");
        _linkForgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent env) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI("https://help.mojang.com/customer/portal/articles/329524-change-or-forgot-password"));
                } catch (IOException e) {
                } catch (URISyntaxException e) {
                }
            }
        });
        _linkForgotPassword.setLocation(passwordField.getX() - 8, passwordField.getY() + passwordField.getHeight() + 2);
        markJLabelLink(_linkForgotPassword);
        add(_linkForgotPassword);


        JLabel _linkRegisterAccount = new JLabel("Register Account");
        _linkRegisterAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent env) {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI("https://account.mojang.com/register?agent=minecraft"));
                } catch (IOException e) {
                } catch (URISyntaxException e) {
                }
            }
        });
        _linkRegisterAccount.setLocation(_linkUsernameorEmail.getX(), this.getHeight() - _linkRegisterAccount.getHeight() - 25);
        markJLabelLink(_linkRegisterAccount);
        add(_linkRegisterAccount);
    }

    private void markJLabelLink(JLabel label) {
        label.setSize(new Dimension(100, 20));
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}