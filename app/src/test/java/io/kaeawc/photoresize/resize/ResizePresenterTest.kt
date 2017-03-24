package io.kaeawc.photoresize.resize

import io.kaeawc.photoresize.models.Photo
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ResizePresenterTest {

    @Mock lateinit var photo: Photo
    @Mock lateinit var view: ResizePresenter.ResizeView
    @Mock lateinit var presenter: ResizePresenter
    @Mock lateinit var interactor: ResizeInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        whenever(presenter.interactor).thenReturn(interactor)
        whenever(presenter.setView(eq(view))).thenCallRealMethod()
        whenever(presenter.onExit(eq(photo), any(), any(), any())).thenCallRealMethod()
        whenever(presenter.onPhotoLoaded(eq(photo))).thenCallRealMethod()
    }

    @Test
    fun `setting the view triggers requesting a photo`() {
        presenter.setView(view)
        verify(interactor, times(1)).requestPhoto()
    }

    @Test
    fun `when photo is loaded presenter will ask view to render it`() {
        presenter.setView(view)
        presenter.onPhotoLoaded(photo)
        verify(view, times(1)).showPhoto(eq(photo))
    }
}
