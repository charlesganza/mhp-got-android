package app.mhp.got.ui.houses.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.mhp.got.R
import app.mhp.got.databinding.HouseDetailsFragmentBinding
import app.mhp.got.ui.utils.makeBold
import app.mhp.got.utils.emptyStringHandler
import com.google.android.material.chip.Chip
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class HouseDetails : Fragment() {

    private val house: HouseDetailsArgs by navArgs()
    private var _binding: HouseDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(ViewPumpContextWrapper.wrap(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HouseDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        //make some textviews bold
        activity?.makeBold(
            binding.name,
            binding.words,
            binding.region,
            binding.coatOfArms,
            binding.currentLord,
            binding.heir,
            binding.overlord
        )

        binding.name.text = house.house.name
        binding.words.text = house.house.words.emptyStringHandler()
        binding.region.text = house.house.region.emptyStringHandler()
        binding.coatOfArms.text = house.house.coatOfArms.emptyStringHandler()
        binding.currentLord.text = house.house.currentLord.emptyStringHandler()
        binding.heir.text = house.house.heir.emptyStringHandler()
        binding.overlord.text = house.house.overlord.emptyStringHandler()

        //for details in lists, use chips
        house.house.titles.forEach { title ->
            binding.titles.addView(createChip(title))
        }

        house.house.seats.forEach { title ->
            binding.seats.addView(createChip(title))
        }

        house.house.ancestralWeapons.forEach { title ->
            binding.ancestralWeapons.addView(createChip(title))
        }
    }

    private fun createChip(title: String): Chip {
        return Chip(context).apply {
            text = title.emptyStringHandler()
            setChipBackgroundColorResource(R.color.colorPrimaryDark)
            setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}