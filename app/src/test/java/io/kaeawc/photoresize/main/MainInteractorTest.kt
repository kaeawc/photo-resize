package io.kaeawc.photoresize.main

import io.kaeawc.photoresize.models.Photo
import io.kaeawc.photoresize.storage.Storage
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainInteractorTest {

    @Mock lateinit var photo: Photo
    @Mock lateinit var storage: Storage
    @Mock lateinit var viewModel: MainInteractor.MainViewModel
    @Mock lateinit var interactor: MainInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        whenever(interactor.storage).thenReturn(storage)
        whenever(interactor.setViewModel(eq(viewModel))).thenCallRealMethod()
        whenever(interactor.destroy()).thenCallRealMethod()
        whenever(interactor.requestPhoto()).thenCallRealMethod()
        whenever(storage.getPhoto()).thenReturn(photo)
    }

    @Test
    fun `requesting a photo triggers view model`() {
        interactor.setViewModel(viewModel)
        interactor.requestPhoto()
        verify(viewModel, times(1)).onPhotoLoaded(eq(photo))
    }
}
