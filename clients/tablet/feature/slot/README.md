# Slot Feature Module

## Overview
The Slot module draws one room's day as a list of slots: what is free, what is booked and by whom,
and what a tap on each of them does. It is the left panel of the main screen, not a screen of its
own.

There is one view — the working day of one room, from now (rounded up to the next quarter) to the
end of office hours. There is no week or month view.

## Features
- Free slots, showing how long they last
- Booked slots, showing the organizer
- A group slot when several bookings fall into one stretch, expanding on tap into the bookings
  inside it
- A placeholder while a booking is still being created on the calendar
- Tapping a free slot or the tablet's own booking opens the booking editor on it
- Re-reading the day every 15 minutes, and again a minute before the first slot starts
- Collapsing every expanded group when the tablet is left untouched

## Architecture

```
slot/
├── di/                          # Koin module: the mapper, the use case and the presenter factory
├── domain/usecase/              # GetSlotsByRoomUseCase
└── presentation/
    ├── SlotComponent.kt         # Presenter and its assisted-injection factory
    ├── SlotIntent.kt            # Taps, date changes, inactivity
    ├── SlotUi.kt                # The four kinds of slot the list can hold
    ├── State.kt                 # The slot list
    ├── components/              # SlotView and the views per slot kind
    └── mapper/                  # Domain Slot to SlotUi
```

## Key Components
- **SlotComponent**: a presenter, not a ViewModel — it has no lifecycle of its own and runs on the
  scope its owner, `MainViewModel`, hands it. Koin builds it through `SlotComponentFactory`,
  supplying the use cases while the owner supplies the scope, the room name and the callback that
  opens the editor. It keeps expanded groups expanded across a refresh, and publishes an empty list
  rather than nothing so a day without free slots does not show the previous day's.
- **SlotUi**: `SimpleSlot`, `MultiSlot`, `NestedSlot` and `LoadingSlot` — what the list is made of.
- **SlotView**: routes a slot to the view for its kind and decides whether it takes a tap at all.
- **GetSlotsByRoomUseCase**: cuts a room's bookings into slots over `SlotUseCase` from the domain
  module.

## Bookings the tablet may not touch

Every tablet books through one shared account, and the calendar only lets that account change what
it organised. The backend says which is which per booking, in `isEditable`: true for what was booked
from a tablet, false for what someone made from their own calendar. A booking of the second kind is
the tablet's to show and nothing more.

So an event slot whose booking is not editable **takes no taps at all** (`SlotView`), rather than
opening the editor on one — the editor would come up with its "Confirm changes" and "Delete booking"
already dead, since `bookingEditor` gates both on the same flag, leaving the reader to guess why.
Free slots and the tablet's own bookings are unaffected; a multi-slot still expands, and the rule
applies to each booking inside it.

`DummyCalendarProvider` never sets `isEditable`, so on a stand without Google every booking looks
editable and this path cannot be exercised there.

## Integration
The Slot module integrates with:
- Core domain module, for `Slot`, `SlotUseCase`, `RoomInfoUseCase` and the timer
- Core UI module, for the strings, the palette and the typography
- Main module, which owns the presenter and turns its editor requests into a modal

## Development
### Changing How a Day Is Cut Into Slots
The cutting itself is `SlotUseCase` in the domain module; `GetSlotsByRoomUseCase` only decides where
the day starts and ends. What each kind of slot looks like is in `components/`, and which domain slot
becomes which `SlotUi` is in `mapper/SlotUiMapper.kt`.
