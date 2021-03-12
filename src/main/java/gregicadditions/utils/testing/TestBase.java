package gregicadditions.utils.testing;

import gregtech.api.util.ItemStackHashStrategy;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;

/**
 * A base class for testing that provides some
 * simple tools to be used by all test cases.
 */
public class TestBase {

    /**
     * Hashing strategy used for Maps where the key is an ItemStack.
     */
    protected static final Hash.Strategy<ItemStack> strategy =
            ItemStackHashStrategy.builder()
                    .compareCount(true)
                    .compareDamage(true)
                    .compareItem(true)
                    .build();
}
