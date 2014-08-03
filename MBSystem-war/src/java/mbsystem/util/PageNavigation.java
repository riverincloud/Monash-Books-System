/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbsystem.util;

/**
 *
 * @author Di
 */
public enum PageNavigation {

    CREATE("Create"),
    EDIT("Edit"),
    LIST("List"),
    VIEW("View"),
    INDEX("Index");

    private final String text;

    PageNavigation(final String s) {
        this.text = s;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
