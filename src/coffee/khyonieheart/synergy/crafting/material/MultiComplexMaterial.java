package coffee.khyonieheart.synergy.crafting.material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class MultiComplexMaterial extends ComplexMaterial
{
	private Set<ComplexMaterial> materials = new HashSet<>();; 

	public MultiComplexMaterial(
		int count,
		Material... materials
	) {
		super(materials[0], count);
		this.materials.addAll(
			Arrays.asList(materials).stream()
				.map(i -> ComplexMaterial.adapt(i))
				.toList()
		);
	}

	public MultiComplexMaterial(
		int count,
		Tag<Material> materials
	) {
		super(count);
		this.materials.addAll(
			materials.getValues().stream()
				.map(i -> ComplexMaterial.adapt(i))
				.toList()
		);
	}

	@Override
	public boolean matches(
		ItemStack item
	) {
		for (ComplexMaterial i : materials)
		{
			if (i == null && item != null)
			{
				return false;
			}
			
			if (i != null && item == null)
			{
				return false;
			}

			if (i.matches(item))
			{
				return true;
			}
		}
		return false;
	}
}
