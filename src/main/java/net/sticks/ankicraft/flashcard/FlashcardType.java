package net.sticks.ankicraft.flashcard;

import java.io.Serializable;

public enum FlashcardType implements Serializable {
    MultipleChoiceSingle,
    MultipleChoiceMultiple,
    FillInBlank,
    Matching,
    Rearranging,
    Written
}
