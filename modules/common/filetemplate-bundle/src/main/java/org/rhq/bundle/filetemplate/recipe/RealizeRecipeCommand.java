/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.rhq.bundle.filetemplate.recipe;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class RealizeRecipeCommand implements RecipeCommand {

    public String getName() {
        return "realize";
    }

    public void parse(RecipeParser parser, RecipeContext context, String[] args) {
        String sopts = ":f:";
        LongOpt[] lopts = { new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f') };

        String file = null;

        Getopt getopt = new Getopt(getName(), args, sopts, lopts);
        int code;

        while ((code = getopt.getopt()) != -1) {
            switch (code) {
            case ':':
            case '?': {
                throw new IllegalArgumentException("Bad recipe command.");
            }

            case 1: {
                throw new IllegalArgumentException("Bad recipe command!");
            }

            case 'f': {
                file = getopt.getOptarg();
                break;
            }

            default: {
                throw new IllegalArgumentException("Unexpected error in recipe command");
            }

            }
        }

        if (file == null) {
            throw new IllegalArgumentException("Did not specify the file to realize");
        }

        context.addRealizedFile(file);
        return;
    }
}
