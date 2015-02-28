package com.maverick.apps.weatherapp.service.tasks;

import com.maverick.apps.weatherapp.models.Properties;

/**
 * Copyright (C) 2014-2015 Techie Digital Benchwork Inc., Ltd. All rights reserved.
 *
 * Mobility Development Division, Digital Media & Communications Business, Techie Digital
 * Benchwork., Ltd.
 *
 * This software and its documentation are confidential and proprietary information of Techie
 * Digital Benchwork., Ltd.  No part of the software and documents may be copied, reproduced,
 * transmitted, translated, or reduced to any electronic medium or machine-readable form without the
 * prior written consent of Techie Digital Benchwork Inc.
 *
 * Techie Digital Benchwork makes no representations with respect to the contents, and assumes no
 * responsibility for any errors that might appear in the software and documents. This publication
 * and the contents hereof are subject to change without notice.
 *
 * History 2015.Feb.28      Phil Pham         The 1st Sprint Version
 */
public class Action {
  public static final String TAG = Properties.PREFIX + Action.class.getSimpleName();

  public static final String REQUEST_OWNER       = "com.maverick.apps.WeatherApp.Action.REQUEST_OWNER";
  public static final String REQUEST_MSG         = "com.maverick.apps.WeatherApp.Action.REQUEST_MSG";
  public static final String OBJECT              = "com.maverick.apps.WeatherApp.Action.OBJECT";
}
