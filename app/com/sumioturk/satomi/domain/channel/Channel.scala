package com.sumioturk.satomi.domain.channel

import com.sumioturk.satomi.domain.user.User

/**
 * (C) Copyright 2013 OMCAS Inc.
 * User: sumioturk
 * Date: 4/21/13
 * Time: 1:16 PM
 *
 */

case class Channel(id: String, name: String, users: List[User])
