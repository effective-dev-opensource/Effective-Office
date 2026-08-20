# Settings Feature Module

## Overview
Settings is the one-time setup screen: it asks which meeting room this tablet is hung outside of.
That single answer is the whole of the module. There is no theme switch, no account, no language
picker and no notification preferences — the tablet has no users to have preferences.

It is the start destination until a room has been chosen, and unreachable afterwards from within the
app.

## Features
- Show the meeting rooms the server knows about, falling back to the built-in list until the server
  answers
- Mark the room this tablet is currently set up with
- Save the chosen room and go on to the main screen
- Report the error and stay put if the room list cannot be fetched

## Architecture

```
settings/
├── components/           # CardRoom, GridRooms, TitleView, ChooseButtonView, ExitButtonView
├── di/                   # Koin module
├── Intent.kt             # User actions
├── SettingsScreen.kt     # The screen and its stateless view
├── SettingsViewModel.kt  # Room list, current room, navigation events
└── State.kt              # UI state
```

## Key Components
- **SettingsScreen**: draws the loading state, the error text, or the room grid, and turns the
  ViewModel's navigation events into calls on the host.
- **SettingsViewModel**: refreshes the room cache and reads the room names on start, writes the
  chosen room through `SetRoomUseCase`, and emits `SettingsNavEvent` — `NavigateToMain` when a room
  is chosen or saved, `ExitApp` from the exit button. The host wires only the first of the two.
- **GridRooms** / **CardRoom**: the two-column grid of rooms, with the current one marked.

## Integration
The Settings module integrates with:
- Core domain module, for `CheckSettingsUseCase`, `SetRoomUseCase` and `RoomInfoUseCase`
- Core UI module, for the loading screen and the palette
- ComposeApp module, which hosts the screen as the `SettingsRoute` destination and pops it off the
  back stack when it navigates to Main

## Development
### Adding a New Setting
There is one setting today, and it is stored by `SettingsManager` in the domain module. A second one
would go the same way: a key in `SettingsManager`, a use case beside `SetRoomUseCase`, a field in
`State`, an action in `Intent`, and a control in `SettingsScreen`.
