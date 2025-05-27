package band.effective.office.elevator

val EffectiveLightColors = EffectiveThemeColors(
    background = EffectiveThemeColors.Background(
        primary = neutral10,
        secondary = neutral15,
        tertiary = neutral15.copy(alpha = 0.8f),
        additional = neutral25,
    ),

    text = EffectiveThemeColors.Text(
        primary = neutral90,
        secondary = neutral45,
        tertiary = orange40,
        accent = orange60,
        primaryEvent = neutral10,
        error = red50,
        additional = neutral30,
        caption = neutral20,
        optional = purple40,
    ),

    icon = EffectiveThemeColors.Icon(
        primary = neutral90,
        secondary = neutral30,
        tertiary = neutral20,
        accent = orange60,
        success = green50,
        error = red50,
        primaryEvent = neutral10,
        optional = purple40,
    ),

    stroke = EffectiveThemeColors.Stroke(
        primary = neutral20,
        error = red50,
        accent = orange60,
    ),

    divider = EffectiveThemeColors.Divider(
        primary = neutral20,
    ),

    graph = EffectiveThemeColors.Graph(
        orange = orange50,
        violet = purple30,
    ),

    button = EffectiveThemeColors.Button(
        primaryNormal = orange60,
        primaryPress = orange50,
        primaryDisable = orange20,
        secondaryNormal = neutral15,
        secondaryPress = neutral10,
        iconNormal = neutral10,
        iconPress = neutral15,
        iconDisable = neutral10,
        tertiaryNormal = neutral10,
        tertiaryPress = neutral15,
        actionEditNormal = purple40,
        actionEditPress = purple30,
        actionDeleteNormal = red50,
        actionDeletePress = red30,
        timeNormal = neutral5,
        timePress = neutral10,
        timeActive = neutral5,
        fullDayNormal = neutral5,
        fullDayPress = neutral10,
        repeatNormal = neutral5,
        repeatPress = neutral10,
        toggleOn = green50,
        toggleOff = neutral25,
        toggleNormal = neutral10,
    ),

    calendar = EffectiveThemeColors.Calendar(
        date = purple40,
    ),

    item = EffectiveThemeColors.Item(
        colleagueNormal = neutral10,
        colleagueRepeat = neutral5,
        repeatNormal = neutral5,
        repeatPress = neutral15,
    ),

    avatar = EffectiveThemeColors.Avatar(
        default = neutral15,
    ),

    card = EffectiveThemeColors.Card(
        normal = neutral10,
        active = neutral10,
        press = neutral15,
    ),

    input = EffectiveThemeColors.Input(
        freeNormal = neutral10,
        freePress = neutral15,
        freeTyping = neutral10,
        freeFill = neutral10,
        lockNormal = neutral15,
        lockPress = neutral10,
        lockTyping = neutral15,
        lockFill = neutral15,
        lockDisable = neutral10,
        searchNormal = neutral10,
        searchPress = neutral15,
        searchTyping = neutral10,
        searchFill = neutral10,
    ),

    table = EffectiveThemeColors.Table(
        tableAvailable = purple10,
        tableSelect = purple50,
    ),
)
