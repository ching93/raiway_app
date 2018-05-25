/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author User
 */
class DateTimeBox extends JPanel {
        private JTextField yearBox;
        private JTextField monthBox;
        private JTextField dayBox;
        private JTextField hourBox;
        private JTextField minBox;
        private JTextField secBox;
        private int type;
        public DateTimeBox(int type) {
            this.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, true));
            // 1 - только время, 2 - и время и дата
            this.type = type;
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            if (type==2) {
                yearBox = new JTextField(); this.add(new LabeledComponent("ГГГГ",yearBox));
                monthBox = new JTextField(); this.add(new LabeledComponent("ММ",monthBox));
                dayBox = new JTextField(); this.add(new LabeledComponent("ДД",dayBox));
            }
            hourBox = new JTextField(); this.add(new LabeledComponent("ЧЧ",hourBox));
            minBox = new JTextField(); this.add(new LabeledComponent("мм",minBox));
            secBox = new JTextField(); this.add(new LabeledComponent("СС",secBox));
            this.validate();
        }
        public Duration getDuration() {
            int hour=0; int min=0; int sec=0;
            if (type==1) {
                hour = Integer.parseInt(hourBox.getText());
                min = Integer.parseInt(minBox.getText());
                sec = Integer.parseInt(secBox.getText());
                Duration result = Duration.ZERO.plusHours(hour).plusMinutes(min).plusSeconds(sec);
                return result;
            }
            throw new IllegalArgumentException("Доступно только время");
        }
        public LocalDateTime getDateTime() {
            int year=0; int month=0; int day=0; int hour=0; int min=0; int sec=0;
            year = Integer.parseInt(yearBox.getText());
            month = Integer.parseInt(monthBox.getText());
            day = Integer.parseInt(dayBox.getText());
            hour = Integer.parseInt(hourBox.getText());
            min = Integer.parseInt(minBox.getText());
            sec = Integer.parseInt(secBox.getText());
            LocalDateTime result = LocalDateTime.of(year, month, month, hour, min);
            return result;
        }
        public void setDateTime(LocalDateTime date) {
            if (type==1)
                throw new IllegalArgumentException("Доступны только время и дата");
            yearBox.setText(Integer.toString(date.getYear()));
            monthBox.setText(Integer.toString(date.getMonthValue()));
            dayBox.setText(Integer.toString(date.getDayOfMonth()));
            hourBox.setText(Integer.toString(date.getHour()));
            minBox.setText(Integer.toString(date.getMinute()));
            secBox.setText(Integer.toString(date.getSecond()));
        }
        public void setDuration(Duration time) {
            if (type==1) {
                long hours = time.toHours();
                long minutes = time.toMinutes() - hours*60;
                long secs = time.getSeconds() - hours*60 - minutes*60;
                hourBox.setText(Long.toString(hours));
                minBox.setText(Long.toString(minutes));
                secBox.setText(Long.toString(secs));
            }
            if (type==2)
                throw new IllegalArgumentException("Доступно только время");
        }
    }
